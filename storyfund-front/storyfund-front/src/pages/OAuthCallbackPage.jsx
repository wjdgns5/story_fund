// src/pages/OAuthCallbackPage.jsx
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get("token");

    if (token) {
      localStorage.setItem("accessToken", token); // 토큰 저장
      navigate("/boards", { replace: true }); // 게시판으로 이동
    } else {
      navigate("/login", { replace: true });
    }
  }, []);

  return (
    <div style={{ textAlign: "center", padding: "100px 20px" }}>
      <p>로그인 처리 중...</p>
    </div>
  );
}

export default OAuthCallbackPage;
