import React, { useState } from "react";
import { HiMenu, HiX } from "react-icons/hi"; // icon menu

const Header = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <header
      className="shadow-md text-white"
      style={{ backgroundColor: "#7157ff" }}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex gap-4 justify-between h-16 items-center">
          <div className="flex gap-4 items-center">
            {/* Logo */}
            <div className="flex-shrink-0">
              <h1 className="text-xl font-bold text-gray-800">JobFinder</h1>
            </div>

            {/* Menu desktop */}
            <nav className="hidden md:flex space-x-6">
              <a href="/" className="hover:text-blue-600 font-medium">
                Home
              </a>
              <a href="/jobs" className=" hover:text-blue-600 font-medium">
                Việc làm
              </a>
              <a href="/companies" className=" hover:text-blue-600 font-medium">
                Công ty
              </a>
              <a href="/contact" className=" hover:text-blue-600 font-medium">
                Liên hệ
              </a>
            </nav>
          </div>

          {/* Auth buttons desktop */}
          <div className="hidden md:flex space-x-4">
            <a
              href="/dang-nhap"
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition"
            >
              Đăng nhập
            </a>
            <a
              href="/dang-ky"
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
            >
              Đăng ký
            </a>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center">
            <button onClick={() => setIsOpen(!isOpen)}>
              {isOpen ? (
                <HiX className="h-6 w-6 text-gray-700" />
              ) : (
                <HiMenu className="h-6 w-6 text-gray-700" />
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {isOpen && (
        <div className="md:hidden bg-gradient-to-r from-blue-100 via-pink-100 to-yellow-100 px-2 pt-2 pb-4 space-y-1">
          <a
            href="/"
            className="block px-3 py-2 rounded-md text-gray-700 hover:bg-blue-200"
          >
            Home
          </a>
          <a
            href="/jobs"
            className="block px-3 py-2 rounded-md text-gray-700 hover:bg-blue-200"
          >
            Việc làm
          </a>
          <a
            href="/companies"
            className="block px-3 py-2 rounded-md text-gray-700 hover:bg-blue-200"
          >
            Công ty
          </a>
          <a
            href="/contact"
            className="block px-3 py-2 rounded-md text-gray-700 hover:bg-blue-200"
          >
            Liên hệ
          </a>
          <a
            href="/dang-nhap"
            className="block px-3 py-2 rounded-md bg-green-600 text-white hover:bg-green-700"
          >
            Đăng nhập
          </a>
          <a
            href="/dang-ky"
            className="block px-3 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700"
          >
            Đăng ký
          </a>
        </div>
      )}
    </header>
  );
};

export default Header;
