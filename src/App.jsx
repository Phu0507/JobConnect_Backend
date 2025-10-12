import { Link, NavLink } from 'react-router'
import './App.css'

function App() {
  return (
    <div className='text-blue-500 bg-red-600 text-2xl font-bold'>
      Hello vite
       <NavLink to="/thong-tin-ca-nhan">All Concerts</NavLink>
    </div>
  )
}

export default App
