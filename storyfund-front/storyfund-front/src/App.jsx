import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import BoardListPage from "./pages/BoardListPage";
import BoardDetailPage from "./pages/BoardDetailPage";
import BoardCreatePage from "./pages/BoardCreatePage";
import BoardEditPage from "./pages/BoardEditPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 기본 경로는 게시판 목록으로 */}
        <Route path="/" element={<Navigate to="/boards" />} />
        {/* 인증 */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        {/* 게시판 */}
        <Route path="/boards" element={<BoardListPage />} />
        <Route path="/boards/create" element={<BoardCreatePage />} />{" "}
        {/* ← :id 보다 위 */}
        <Route path="/boards/:id/edit" element={<BoardEditPage />} />{" "}
        {/* ← 추가 */}
        <Route path="/boards/:id" element={<BoardDetailPage />} />{" "}
        {/* ← 아래 */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
