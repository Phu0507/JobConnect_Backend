import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";
import { BrowserRouter, Route, Routes } from "react-router";
import Login from "./pages/Login/Login.jsx";
import Register from "./pages/Register/Register.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/dang-nhap" element={<Login />} />
        <Route path="/dang-ky" element={<Register />} />
        <Route path="/chi-tiet-cong-viec" element={<App />} />
        <Route path="/ho-so-ca-nhan" element={<App />} />
      </Routes>
    </BrowserRouter>
    ,
  </StrictMode>
);
