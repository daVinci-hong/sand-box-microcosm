import http from 'k6/http';
import { check } from 'k6';

// =================================================================
// == 【【【 唯一的、核心的修正：校準“最終的風暴” 】】】 ==
// =================================================================
export const options = {
  stages: [
    // 在 10 秒內，將並發用戶數，提升至 30。
    // 這個強度，足以，同時，觸發，20/s 的“限流器”，與，10併發的“艙壁”。
    { duration: '10s', target: 30 },
    // 維持 30 個並發用戶，持續 20 秒。
    // 這個時長，足以，讓“超時”的失敗，累積，並，觸發“熔斷器”。
    { duration: '20s', target: 30 },
    // 在 10 秒內，讓風暴平息。
    { duration: '10s', target: 0 },
  ],
};

// 【【【 自動獲取 JWT Token 】】】
export function setup() {
  console.log('正在獲取 JWT Token...');
  const tokenResponse = http.get('http://localhost:8080/generate-token/subject/admin/roles/ADMIN');

  if (tokenResponse.status !== 200) {
    console.error(`Token request failed with status: ${tokenResponse.status}`);
    console.error(`Response body: ${tokenResponse.body}`);
    throw new Error(`Failed to get JWT token: ${tokenResponse.status}`);
  }

  const token = tokenResponse.body.replace(/"/g, ''); // 移除引號
  console.log(`JWT Token 獲取成功: ${token.substring(0, 20)}...`);
  return { jwtToken: token };
}

export default function (data) {
  const params = {
    headers: { 'Authorization': `Bearer ${data.jwtToken}` },
  };
  // 攻擊目標：我們唯一的、能夠，觸發“超時”的“慢速靶心”。
  const res = http.get('http://localhost:8080/api/beacon/slow-endpoint', params);

  // 驗收標準：一個請求，要麼，成功 (200)，要麼，被“限流器”或“艙壁”拒絕 (429)，
  // 要麼，被“熔斷器”的降級邏輯拒絕 (503)。
  check(res, {
    'is status 200 (Success) OR 429 (Rejected) OR 503 (Fallback)': (r) => r.status === 200 || r.status === 429 || r.status === 503,
  });
}