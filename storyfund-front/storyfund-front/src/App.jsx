import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
// import SignupPage from "./pages/SignupPage";
// import BoardListPage from "./pages/BoardListPage";
// import BoardDetailPage from "./pages/BoardDetailPage";
// import BoardCreatePage from "./pages/BoardCreatePage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 기본 경로는 게시판 목록으로 */}
        <Route path="/" element={<Navigate to="/boards" />} />

        {/* 인증 */}
        <Route path="/login" element={<LoginPage />} />
        {/* <Route path="/signup" element={<SignupPage />} /> */}

        {/* 게시판 */}
        {/* <Route path="/boards" element={<BoardListPage />} />
        <Route path="/boards/:id" element={<BoardDetailPage />} />
        <Route path="/boards/create" element={<BoardCreatePage />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
