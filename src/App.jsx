import { Link, NavLink } from "react-router";
import "./App.css";
import Header from "./components/Header";
import JobSearch from "./components/Search";

function App() {
  return (
    <div className="">
      <Header />
      <JobSearch />
    </div>
  );
}

export default App;
