import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";

function BoardEditPage() {
  const { id } = useParams(); // URL 에서 게시글 id 꺼내기
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [isPaid, setIsPaid] = useState(false);
  const [loading, setLoading] = useState(true); // 기존 데이터 로딩
  const [error, setError] = useState("");

  // 기존 게시글 데이터 불러오기
  useEffect(() => {
    fetchBoard();
  }, [id]);

  const fetchBoard = async () => {
    try {
      const response = await api.get(`/api/boards/${id}`);
      const board = response.data;

      // 기존 데이터를 input 에 미리 채워넣기
      setTitle(board.title);
      setContent(board.content);
      setIsPaid(board.isPaid);
    } catch (err) {
      setError("게시글을 불러올 수 없습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 수정 저장
  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 입력해주세요.");
      return;
    }

    try {
      await api.put(`/api/boards/${id}`, { title, content, isPaid });
      alert("수정됐습니다.");
      navigate(`/boards/${id}`); // 수정 후 상세 페이지로 이동
    } catch (err) {
      setError(err.response?.data?.message || "수정에 실패했습니다.");
    }
  };

  // ── 로딩 중 ──
  if (loading) {
    return <div style={styles.center}>불러오는 중...</div>;
  }

  // ── 에러 ──
  if (error && !title) {
    return (
      <div style={styles.center}>
        <p style={{ color: "red" }}>{error}</p>
        <button style={styles.cancelButton} onClick={() => navigate("/boards")}>
          목록으로
        </button>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      {/* 뒤로가기 */}
      <button
        style={styles.backButton}
        onClick={() => navigate(`/boards/${id}`)}
      >
        ← 게시글로
      </button>

      <h2 style={styles.pageTitle}>게시글 수정</h2>

      {/* 제목 */}
      <input
        style={styles.input}
        type="text"
        placeholder="제목 (최대 100자)"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        maxLength={100}
      />

      {/* 글자 수 표시 */}
      <p style={styles.charCount}>{title.length} / 100</p>

      {/* 내용 */}
      <textarea
        style={styles.textarea}
        placeholder="내용을 입력해주세요. (최대 5000자)"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={5000}
      />

      {/* 글자 수 표시 */}
      <p style={styles.charCount}>{content.length} / 5000</p>

      {/* 유료 여부 */}
      <label style={styles.checkLabel}>
        <input
          type="checkbox"
          checked={isPaid}
          onChange={(e) => setIsPaid(e.target.checked)}
          style={{ marginRight: "8px", width: "16px", height: "16px" }}
        />
        유료 게시글로 설정 (코인 1개 필요)
      </label>

      {/* 에러 메시지 */}
      {error && <p style={styles.error}>{error}</p>}

      {/* 버튼 */}
      <div style={styles.buttonRow}>
        <button
          style={styles.cancelButton}
          onClick={() => navigate(`/boards/${id}`)}
        >
          취소
        </button>
        <button style={styles.submitButton} onClick={handleSubmit}>
          수정 저장
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "800px",
    margin: "40px auto",
    padding: "0 20px 60px",
    display: "flex",
    flexDirection: "column",
    gap: "10px",
  },
  center: {
    textAlign: "center",
    padding: "80px 20px",
  },
  backButton: {
    background: "none",
    border: "none",
    color: "#4F8EF7",
    cursor: "pointer",
    fontSize: "14px",
    padding: "0",
    textAlign: "left",
  },
  pageTitle: {
    margin: "8px 0 4px",
    fontSize: "22px",
    fontWeight: "600",
  },
  input: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "15px",
    outline: "none",
  },
  textarea: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "15px",
    minHeight: "320px",
    resize: "vertical",
    lineHeight: "1.8",
    outline: "none",
  },
  charCount: {
    fontSize: "12px",
    color: "#aaa",
    margin: "0",
    textAlign: "right",
  },
  checkLabel: {
    display: "flex",
    alignItems: "center",
    fontSize: "14px",
    color: "#333",
    cursor: "pointer",
    marginTop: "4px",
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
    marginTop: "8px",
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

export default BoardEditPage;
