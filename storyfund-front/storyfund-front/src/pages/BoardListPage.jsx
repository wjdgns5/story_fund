import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function BoardListPage() {
  const [boards, setBoards] = useState([]); // 게시글 목록
  const [page, setPage] = useState(0); // 현재 페이지
  const [totalPages, setTotalPages] = useState(0); // 전체 페이지 수
  const [keyword, setKeyword] = useState(""); // 검색어
  const [search, setSearch] = useState(""); // 실제 검색 실행값
  const [loading, setLoading] = useState(false); // 로딩 상태
  const [isLogin, setIsLogin] = useState(false); // 로그인 여부

  const navigate = useNavigate();

  // 로그인 여부 확인
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setIsLogin(!!token); // token 있으면 true, 없으면 false
  }, []);

  // 게시글 목록 불러오기
  // page 또는 search 가 바뀔 때마다 실행
  useEffect(() => {
    fetchBoards();
  }, [page, search]);

  const fetchBoards = async () => {
    setLoading(true);
    try {
      const params = { page };
      if (search) params.keyword = search; // 검색어 있으면 추가

      const response = await api.get("/api/boards", { params });
      setBoards(response.data.content); // 게시글 목록
      setTotalPages(response.data.totalPages); // 전체 페이지 수
    } catch (err) {
      console.error("게시글 목록 조회 실패", err);
    } finally {
      setLoading(false);
    }
  };

  // 검색 실행
  const handleSearch = () => {
    setPage(0); // 검색 시 첫 페이지로
    setSearch(keyword);
  };

  // 로그아웃
  const handleLogout = async () => {
    try {
      await api.post("/api/auth/logout");
    } catch (err) {
      console.error(err);
    } finally {
      localStorage.removeItem("accessToken");
      setIsLogin(false);
      navigate("/login");
    }
  };

  return (
    <div style={styles.container}>
      {/* 헤더 */}
      <div style={styles.header}>
        <h2 style={{ margin: 0 }}>📋 StoryFund 게시판</h2>
        <div style={styles.headerRight}>
          {isLogin ? (
            <>
              <button
                style={styles.button}
                onClick={() => navigate("/boards/create")}
              >
                글쓰기
              </button>
              <button
                style={{ ...styles.button, background: "#6c757d" }}
                onClick={handleLogout}
              >
                로그아웃
              </button>
            </>
          ) : (
            <button style={styles.button} onClick={() => navigate("/login")}>
              로그인
            </button>
          )}
        </div>
      </div>

      {/* 검색 */}
      <div style={styles.searchRow}>
        <input
          style={styles.searchInput}
          type="text"
          placeholder="제목 검색"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
        />
        <button style={styles.button} onClick={handleSearch}>
          검색
        </button>
      </div>

      {/* 로딩 */}
      {loading && <p style={styles.center}>불러오는 중...</p>}

      {/* 게시글 목록 */}
      {!loading && boards.length === 0 && (
        <p style={styles.center}>게시글이 없습니다.</p>
      )}

      {!loading &&
        boards.map((board) => (
          <div
            key={board.id}
            style={styles.boardItem}
            onClick={() => navigate(`/boards/${board.id}`)}
          >
            <div style={styles.boardTitle}>
              {board.isPaid && <span style={styles.paidBadge}>유료</span>}
              {board.title}
            </div>
            <div style={styles.boardMeta}>
              <span>{board.authorNickname}</span>
              <span>조회 {board.viewCount}</span>
              <span>
                {new Date(board.createdAt).toLocaleDateString("ko-KR")}
              </span>
            </div>
          </div>
        ))}

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div style={styles.pagination}>
          <button
            style={styles.pageButton}
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            이전
          </button>

          {[...Array(totalPages)].map((_, i) => (
            <button
              key={i}
              style={{
                ...styles.pageButton,
                background: i === page ? "#4F8EF7" : "white",
                color: i === page ? "white" : "#333",
              }}
              onClick={() => setPage(i)}
            >
              {i + 1}
            </button>
          ))}

          <button
            style={styles.pageButton}
            disabled={page === totalPages - 1}
            onClick={() => setPage(page + 1)}
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "800px",
    margin: "40px auto",
    padding: "0 20px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "24px",
  },
  headerRight: {
    display: "flex",
    gap: "8px",
  },
  searchRow: {
    display: "flex",
    gap: "8px",
    marginBottom: "16px",
  },
  searchInput: {
    flex: 1,
    padding: "10px 14px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
  },
  button: {
    padding: "10px 18px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
  boardItem: {
    padding: "16px",
    border: "1px solid #eee",
    borderRadius: "8px",
    marginBottom: "10px",
    cursor: "pointer",
    transition: "background 0.15s",
  },
  boardTitle: {
    fontSize: "16px",
    fontWeight: "500",
    marginBottom: "8px",
    display: "flex",
    alignItems: "center",
    gap: "8px",
  },
  paidBadge: {
    fontSize: "11px",
    padding: "2px 8px",
    background: "#FFD700",
    color: "#333",
    borderRadius: "99px",
    fontWeight: "600",
  },
  boardMeta: {
    display: "flex",
    gap: "16px",
    fontSize: "13px",
    color: "#888",
  },
  center: {
    textAlign: "center",
    color: "#888",
    padding: "40px 0",
  },
  pagination: {
    display: "flex",
    justifyContent: "center",
    gap: "6px",
    marginTop: "24px",
  },
  pageButton: {
    padding: "8px 14px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
};

export default BoardListPage;
