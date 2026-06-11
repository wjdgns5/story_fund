import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function LoginPage() {
  // 입력값 상태
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate(); // 페이지 이동 함수

  // 로그인 버튼 클릭
  const handleLogin = async () => {
    try {
      const response = await api.post("/api/auth/login", { email, password });

      // Access Token 저장
      localStorage.setItem("accessToken", response.data.accessToken);

      // 게시판으로 이동
      navigate("/boards");
    } catch (err) {
      setError(err.response?.data?.message || "로그인에 실패했습니다.");
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>로그인</h2>

      <input
        style={styles.input}
        type="email"
        placeholder="이메일"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <input
        style={styles.input}
        type="password"
        placeholder="비밀번호"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      {/* 에러 메시지 */}
      {error && <p style={styles.error}>{error}</p>}

      <button style={styles.button} onClick={handleLogin}>
        로그인
      </button>

      <p style={styles.link}>
        계정이 없으신가요?{" "}
        <span
          style={{ color: "#4F8EF7", cursor: "pointer" }}
          onClick={() => navigate("/signup")}
        >
          회원가입
        </span>
      </p>
    </div>
  );
}

// 간단한 인라인 스타일
const styles = {
  container: {
    maxWidth: "400px",
    margin: "100px auto",
    padding: "40px",
    border: "1px solid #ddd",
    borderRadius: "8px",
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  title: {
    textAlign: "center",
    marginBottom: "8px",
  },
  input: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
  },
  button: {
    padding: "12px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
  error: {
    color: "red",
    fontSize: "13px",
    margin: "0",
  },
  link: {
    textAlign: "center",
    fontSize: "13px",
    color: "#666",
  },
};

export default LoginPage;
