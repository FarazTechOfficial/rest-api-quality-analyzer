import { useRef, useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router";
import { api } from "../services/api";
import { SAMPLE_SPEC } from "../data/sampleSpec";
import ErrorAlert from "../components/ErrorAlert";
import LoadingSpinner from "../components/LoadingSpinner";

export default function Analyze() {
  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [apiName, setApiName] = useState("");
  const [version, setVersion] = useState("");
  const [specText, setSpecText] = useState("");
  const [fileName, setFileName] = useState("");
  const [error, setError] = useState("");
  const [analyzing, setAnalyzing] = useState(false);

  const hasSpec = specText.trim().length > 0;

  function handleFile(file: File | undefined) {
    if (!file) return;
    setFileName(file.name);
    const reader = new FileReader();
    reader.onload = () => setSpecText(String(reader.result ?? ""));
    reader.onerror = () => setError("The file could not be read. Please try again.");
    reader.readAsText(file);
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!apiName.trim()) {
      setError("Please give the API a name.");
      return;
    }
    if (!hasSpec) {
      setError("Please upload or paste an OpenAPI specification.");
      return;
    }

    setError("");
    setAnalyzing(true);
    try {
      const report = await api.analyze({
        apiName: apiName.trim(),
        version: version.trim() || "1.0",
        spec: specText,
      });
      navigate(`/result/${report.analysisId}`);
    } catch (err) {
      setAnalyzing(false);
      setError(err instanceof Error ? err.message : "Could not analyze the specification.");
    }
  }

  if (analyzing) {
    return (
      <div className="card">
        <LoadingSpinner label="Analyzing your API..." />
        <p className="meta" style={{ textAlign: "center", marginTop: 8 }}>
          The analyzer is parsing and scoring the specification. This can take a
          few seconds.
        </p>
      </div>
    );
  }

  return (
    <div className="card">
      <h1>Analyze API</h1>
      <p className="hero-desc">
        Provide a name and an OpenAPI specification (JSON or YAML). The analyzer checks
        the specification against the implemented research practices.
      </p>

      {error && <ErrorAlert message={error} />}

      <form onSubmit={handleSubmit} noValidate>
        <div className="form-group">
          <label htmlFor="api-name">API Name</label>
          <input
            id="api-name"
            className="input"
            type="text"
            placeholder="My Spring Boot API"
            value={apiName}
            onChange={(e) => setApiName(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="api-version">Version</label>
          <input
            id="api-version"
            className="input"
            type="text"
            placeholder="1.0"
            value={version}
            onChange={(e) => setVersion(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="spec-file">OpenAPI Specification</label>
          <div className="file-box">
            <input
              ref={fileInputRef}
              id="spec-file"
              className="file-input"
              type="file"
              accept=".json,.yaml,.yml,application/json,application/yaml"
              onChange={(e) => handleFile(e.target.files?.[0])}
            />
            <span className="file-name">{fileName || "Choose a JSON or YAML file"}</span>
          </div>
          <p className="hint">Or paste the specification below.</p>
          <textarea
            className="textarea"
            placeholder='{"openapi": "3.0.0", "paths": { "/users": { "get": {} } } }'
            value={specText}
            onChange={(e) => setSpecText(e.target.value)}
            aria-label="OpenAPI specification text"
          />
          <p className="hint">
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => {
                setSpecText(SAMPLE_SPEC);
                setFileName("");
              }}
            >
              Load sample specification
            </button>{" "}
            — fills the box with a small example spec.
          </p>
        </div>

        <button type="submit" className="btn btn-primary">
          Analyze API
        </button>
      </form>
    </div>
  );
}
