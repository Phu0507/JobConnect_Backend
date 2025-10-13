import React, { useState, useRef, useEffect } from "react";

const MultiSelect = ({ options, placeholder = "Chọn...", onChange }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState([]);
  const dropdownRef = useRef(null);

  // Đóng dropdown khi click ra ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const toggleSelect = (option) => {
    let newSelected;
    if (selected.some((s) => s.value === option.value)) {
      newSelected = selected.filter((s) => s.value !== option.value);
    } else {
      newSelected = [...selected, option];
    }
    setSelected(newSelected);
    onChange && onChange(newSelected);
  };

  // Hiển thị text trên button
  const displayText = () => {
    if (selected.length === 0) return placeholder;
    if (selected.length === 1) return selected[0].label;
    return `${selected[0].label} (+${selected.length - 1})`;
  };

  return (
    <div className="relative w-64" ref={dropdownRef}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="w-full px-4 py-2 border border-gray-300 rounded-md bg-white text-left focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        {displayText()}
      </button>

      {isOpen && (
        <div className="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-auto">
          {options.map((option) => (
            <label
              key={option.value}
              className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer"
            >
              <input
                type="checkbox"
                checked={selected.some((s) => s.value === option.value)}
                onChange={() => toggleSelect(option)}
                className="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              {option.label}
            </label>
          ))}
        </div>
      )}
    </div>
  );
};

export default MultiSelect;
