import React, { useState } from "react";
import MultiSelect from "./MultiSelect";

// Chuyển locations sang dạng { label, value }
const locations = [
  { label: "Hà Nội", value: "hn" },
  { label: "TP. Hồ Chí Minh", value: "hcm" },
  { label: "Đà Nẵng", value: "dn" },
  { label: "Cần Thơ", value: "ct" },
  { label: "Khác", value: "other" },
];

const jobTypes = [
  { label: "Toàn thời gian", value: "fulltime" },
  { label: "Bán thời gian", value: "parttime" },
  { label: "Thực tập", value: "internship" },
  { label: "Tự do", value: "freelance" },
];

const JobSearch = ({ onSearch }) => {
  const [keyword, setKeyword] = useState("");
  const [jobType, setJobType] = useState([]);
  const [location, setLocation] = useState([]);

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch && onSearch({ keyword, jobType, location });
    console.log({ keyword, jobType, location });
  };

  return (
    <div className="max-w-5xl mx-auto p-4 bg-white rounded-lg shadow-lg mt-4">
      <form
        onSubmit={handleSearch}
        className="flex flex-col md:flex-row gap-3 md:gap-4 items-end"
      >
        {/* Keyword */}
        <input
          type="text"
          placeholder="Nhập từ khóa công việc..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 transition"
        />

        {/* Job Type */}
        <MultiSelect
          options={jobTypes}
          placeholder="Chọn loại công việc"
          onChange={(selectedOptions) => setJobType(selectedOptions)}
        />

        {/* Location */}
        <MultiSelect
          options={locations}
          placeholder="Chọn tỉnh/thành"
          onChange={(selectedOptions) => setLocation(selectedOptions)}
        />

        {/* Search Button */}
        <button
          type="submit"
          className="bg-blue-600 text-white px-5 py-2 rounded-md hover:bg-blue-700 transform transition-transform duration-200 hover:scale-105"
        >
          Tìm kiếm
        </button>
      </form>
    </div>
  );
};

export default JobSearch;
