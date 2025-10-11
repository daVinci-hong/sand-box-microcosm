# Circuit Breaker Test Script
$GatewayBaseUrl = "http://localhost:8080"
$TargetService = "beacon-service"
$TargetEndpoint = "/api/beacon/actuator/health"
$TokenGenerationUrl = "$GatewayBaseUrl/generate-token/subject/test-user/roles/USER,ADMIN"
$TestUrl = "$GatewayBaseUrl$TargetEndpoint"
$RequestsToTrip = 6

Clear-Host

Write-Host "--- Step 1: Stop downstream service ---" -ForegroundColor Yellow
try {
    docker-compose rm -s -f $TargetService
    Write-Host "Service stopped successfully." -ForegroundColor Green
} catch {
    Write-Host "Error stopping service: $_" -ForegroundColor Red
    exit
}

Write-Host "--- Step 2: Generate JWT Token ---" -ForegroundColor Yellow
$token = curl.exe $TokenGenerationUrl
Write-Host "Token obtained successfully." -ForegroundColor Green

Write-Host "--- Step 3: Send requests to trigger circuit breaker ---" -ForegroundColor Yellow
for ($i = 1; $i -le $RequestsToTrip; $i++) {
    Write-Host "  Sending request #$i..."
    $responseCode = curl.exe -s -o "nul" --connect-timeout 5 -w "%{http_code}" -H "Authorization: Bearer $token" $TestUrl
    if ($responseCode -ne "200") {
        Write-Host "  Expected failure. HTTP status: $responseCode" -ForegroundColor Cyan
    } else {
        Write-Host "  Circuit breaker opened! Fallback activated." -ForegroundColor Green
    }
    Start-Sleep -Seconds 1
}

Write-Host "--- Step 4: Verify circuit breaker is OPEN ---" -ForegroundColor Yellow
Write-Host "Next request should be routed to fallback..."
$response = curl.exe -i -H "Authorization: Bearer $token" $TestUrl
if ($response -like "*fallback*") {
    Write-Host "Success! Request routed to fallback." -ForegroundColor Green
    Write-Host $response
} else {
    Write-Host "Warning: Expected fallback response not detected." -ForegroundColor Magenta
    Write-Host $response
}

$WaitDuration = 15
Write-Host "--- Step 5: Wait $WaitDuration seconds for recovery and restart service ---" -ForegroundColor Yellow
Start-Sleep -Seconds $WaitDuration

try {
    Write-Host "Restarting service..."
    docker-compose up -d --build $TargetService
    Write-Host "Service starting in background." -ForegroundColor Green
    Write-Host "Waiting additional 10 seconds for service to be ready..."
    Start-Sleep -Seconds 10
} catch {
    Write-Host "Error starting service: $_" -ForegroundColor Red
    exit
}

Write-Host "--- Step 6: Test system recovery ---" -ForegroundColor Yellow
Write-Host "Sending request to verify circuit breaker is closed..."
$response = curl.exe -i -H "Authorization: Bearer $token" $TestUrl
if ($response -like "*`"status`":`"UP`"*") {
    Write-Host "Test successful! System recovered normally." -ForegroundColor Green
    Write-Host $response
} else {
    Write-Host "Test failed! System not recovered from circuit breaker state." -ForegroundColor Red
    Write-Host $response
}