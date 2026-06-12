import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function SignupPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [code, setCode] = useState(""); // 인증 코드
  const [codeSent, setCodeSent] = useState(false); // 코드 발송 여부
  const [verified, setVerified] = useState(false); // 인증 완료 여부
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();

  // 인증 코드 발송
  const handleSendCode = async () => {
    setError("");
    setMessage("");

    if (!email) {
      setError("이메일을 먼저 입력해주세요.");
      return;
    }

    try {
      await api.post("/api/auth/emails/send", { email });
      setCodeSent(true);
      setMessage("인증 코드를 발송했습니다. 이메일을 확인해주세요.");
    } catch (err) {
      setError(err.response?.data?.message || "코드 발송에 실패했습니다.");
    }
  };

  // 인증 코드 확인
  const handleVerifyCode = async () => {
    setError("");
    setMessage("");

    try {
      await api.post("/api/auth/emails/verify", { email, code });
      setVerified(true);
      setMessage("이메일 인증이 완료됐습니다.");
    } catch (err) {
      setError(err.response?.data?.message || "인증에 실패했습니다.");
    }
  };

  // 회원가입
  const handleSignup = async () => {
    setError("");

    if (!verified) {
      setError("이메일 인증을 완료해주세요.");
      return;
    }

    try {
      await api.post("/api/auth/signup", { email, password, nickname });
      alert("회원가입이 완료됐습니다. 로그인해주세요.");
      navigate("/login");
    } catch (err) {
      setError(err.response?.data?.message || "회원가입에 실패했습니다.");
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>회원가입</h2>

      {/* 이메일 + 인증 코드 발송 */}
      <div style={styles.row}>
        <input
          style={{ ...styles.input, flex: 1 }}
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={verified} // 인증 완료 후 수정 불가
        />
        <button
          style={styles.smallButton}
          onClick={handleSendCode}
          disabled={verified}
        >
          {codeSent ? "재발송" : "인증 코드 발송"}
        </button>
      </div>

      {/* 인증 코드 입력 (발송 후에만 보임) */}
      {codeSent && !verified && (
        <div style={styles.row}>
          <input
            style={{ ...styles.input, flex: 1 }}
            type="text"
            placeholder="인증 코드 6자리"
            value={code}
            onChange={(e) => setCode(e.target.value)}
          />
          <button style={styles.smallButton} onClick={handleVerifyCode}>
            확인
          </button>
        </div>
      )}

      {/* 인증 완료 표시 */}
      {verified && (
        <p style={{ color: "green", fontSize: "13px", margin: "0" }}>
          ✅ 이메일 인증 완료
        </p>
      )}

      {/* 비밀번호 */}
      <input
        style={styles.input}
        type="password"
        placeholder="비밀번호 (8자 이상)"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      {/* 닉네임 */}
      <input
        style={styles.input}
        type="text"
        placeholder="닉네임 (2~10자)"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
      />

      {/* 에러 / 안내 메시지 */}
      {error && <p style={styles.error}>{error}</p>}
      {message && <p style={styles.success}>{message}</p>}

      {/* 회원가입 버튼 */}
      <button style={styles.button} onClick={handleSignup}>
        회원가입
      </button>

      <p style={styles.link}>
        이미 계정이 있으신가요?{" "}
        <span
          style={{ color: "#4F8EF7", cursor: "pointer" }}
          onClick={() => navigate("/login")}
        >
          로그인
        </span>
      </p>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "400px",
    margin: "80px auto",
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
  row: {
    display: "flex",
    gap: "8px",
    alignItems: "center",
  },
  input: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
    width: "100%",
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
  smallButton: {
    padding: "12px 16px",
    background: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "13px",
    cursor: "pointer",
    whiteSpace: "nowrap",
  },
  error: {
    color: "red",
    fontSize: "13px",
    margin: "0",
  },
  success: {
    color: "green",
    fontSize: "13px",
    margin: "0",
  },
  link: {
    textAlign: "center",
    fontSize: "13px",
    color: "#666",
  },
};

export default SignupPage;
