import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function BoardCreatePage() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [isPaid, setIsPaid] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 입력해주세요.");
      return;
    }

    try {
      await api.post("/api/boards", { title, content, isPaid });
      alert("게시글이 등록됐습니다.");
      navigate("/boards");
    } catch (err) {
      setError(err.response?.data?.message || "등록에 실패했습니다.");
    }
  };

  return (
    <div style={styles.container}>
      <button style={styles.backButton} onClick={() => navigate("/boards")}>
        ← 목록으로
      </button>

      <h2 style={styles.title}>게시글 작성</h2>

      <input
        style={styles.input}
        type="text"
        placeholder="제목 (최대 100자)"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        maxLength={100}
      />

      <textarea
        style={styles.textarea}
        placeholder="내용을 입력해주세요. (최대 5000자)"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={5000}
      />

      {/* 유료 여부 */}
      <label style={styles.checkLabel}>
        <input
          type="checkbox"
          checked={isPaid}
          onChange={(e) => setIsPaid(e.target.checked)}
          style={{ marginRight: "8px" }}
        />
        유료 게시글로 설정 (코인 1개 필요)
      </label>

      {error && <p style={styles.error}>{error}</p>}

      <div style={styles.buttonRow}>
        <button style={styles.cancelButton} onClick={() => navigate("/boards")}>
          취소
        </button>
        <button style={styles.submitButton} onClick={handleSubmit}>
          등록하기
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "800px",
    margin: "40px auto",
    padding: "0 20px",
    display: "flex",
    flexDirection: "column",
    gap: "14px",
  },
  backButton: {
    background: "none",
    border: "none",
    color: "#4F8EF7",
    cursor: "pointer",
    fontSize: "14px",
    padding: "0",
  },
  title: {
    margin: "0",
    fontSize: "22px",
  },
  input: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "15px",
  },
  textarea: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "15px",
    minHeight: "300px",
    resize: "vertical",
    lineHeight: "1.7",
  },
  checkLabel: {
    display: "flex",
    alignItems: "center",
    fontSize: "14px",
    color: "#333",
    cursor: "pointer",
  },
  error: {
    color: "red",
    fontSize: "13px",
    margin: "0",
  },
  buttonRow: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "8px",
  },
  cancelButton: {
    padding: "12px 24px",
    background: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
  submitButton: {
    padding: "12px 24px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
};

export default BoardCreatePage;
