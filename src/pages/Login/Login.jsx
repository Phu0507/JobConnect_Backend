import React, { useState } from "react";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [remember, setRemember] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log({ email, password, remember });
    // TODO: gọi API login
  };

  return (
    <div className="bg-gradient-to-r from-purple-300 via-blue-200 to-purple-300 flex items-center justify-center min-h-screen animate-bg-gradient">
      <div className="w-full max-w-sm bg-white p-8 rounded-lg shadow-2xl transform transition-transform duration-500 animate-fade-in">
        <div className="text-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">
            Chào mừng bạn trở lại!
          </h2>
          <p className="text-sm text-gray-500 mt-1">
            Hãy đăng nhập để tiếp tục trải nghiệm ứng dụng
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700"
            >
              Email
            </label>
            <input
              type="email"
              id="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 transition duration-300"
            />
          </div>

          {/* Password */}
          <div>
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-700"
            >
              Mật khẩu
            </label>
            <input
              type="password"
              id="password"
              placeholder="Mật khẩu"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 transition duration-300"
            />
          </div>

          {/* Remember me */}
          <div className="flex items-center">
            <input
              type="checkbox"
              id="remember"
              checked={remember}
              onChange={() => setRemember(!remember)}
              className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded transition duration-300"
            />
            <label
              htmlFor="remember"
              className="ml-2 block text-sm text-gray-900"
            >
              Ghi nhớ đăng nhập
            </label>
          </div>

          {/* Submit */}
          <div>
            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-green-500 transform transition-transform duration-200 hover:scale-105"
            >
              Đăng nhập
            </button>
          </div>
        </form>

        {/* Footer */}
        <div className="mt-4 text-center text-sm text-gray-700 space-y-1">
          <p>
            Chưa có tài khoản?{" "}
            <a href="/dang-ky" className="text-blue-700 hover:underline">
              Đăng ký
            </a>
          </p>
          <p>
            <a href="/" className="text-blue-700 hover:underline">
              Trang chủ
            </a>
          </p>
        </div>
      </div>

      {/* Tailwind animation thêm */}
      <style>
        {`
          @keyframes fade-in {
            0% { opacity: 0; transform: translateY(-20px); }
            100% { opacity: 1; transform: translateY(0); }
          }
          .animate-fade-in {
            animation: fade-in 0.6s ease-out forwards;
          }

          @keyframes gradient-bg {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
          }
          .animate-bg-gradient {
            background-size: 200% 200%;
            animation: gradient-bg 15s ease infinite;
          }
        `}
      </style>
    </div>
  );
};

export default Login;
