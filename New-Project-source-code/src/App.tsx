import { HashRouter, Route, Routes } from "react-router";
import Nav from "./components/Nav";
import Dashboard from "./pages/Dashboard";
import Analyze from "./pages/Analyze";
import Result from "./pages/Result";
import Practices from "./pages/Practices";
import Reports from "./pages/Reports";

export default function App() {
  return (
    <HashRouter>
      <Nav />
      <main className="container page">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/analyze" element={<Analyze />} />
          <Route path="/result/:id" element={<Result />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/practices" element={<Practices />} />
          <Route path="*" element={<Dashboard />} />
        </Routes>
      </main>
      <footer className="footer">
        REST API Design Quality Analyzer — research prototype. The UI reports
        measurements; it does not perform academic interpretation of the results.
      </footer>
    </HashRouter>
  );
}
