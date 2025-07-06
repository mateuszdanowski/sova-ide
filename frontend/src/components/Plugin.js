import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import '../components/ProjectDetail.css';
import '../components/Plugin.css';

const Plugin = ({ plugin, projectId, onExecuted }) => {
  const fileInputRef = useRef(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleExecute = async () => {
    setLoading(true);
    setError(null);
    const formData = new FormData();
    formData.append('pluginName', plugin.name);

    try {
      const response = await fetch(
        `http://localhost:8080/projects/${projectId}/plugins/execute`,
        {
          method: 'POST',
          body: formData,
        }
      );
      if (!response.ok) throw new Error('Execution failed');
      if (onExecuted) onExecuted();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = async (e) => {
    if (!e.target.files.length) return;
    setLoading(true);
    setError(null);
    const formData = new FormData();
    formData.append('file', e.target.files[0]);
    formData.append('pluginName', plugin.name);
    try {
      const response = await fetch(
        `http://localhost:8080/projects/${projectId}/plugins/execute-with-file`,
        {
          method: 'POST',
          body: formData,
        }
      );
      if (!response.ok) throw new Error('File execution failed');
      if (onExecuted) onExecuted();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
      e.target.value = '';
    }
  };

  return (
    <li className="plugin-list-item">
      <div><b>Name:</b> {plugin.name}</div>
      <div><b>Type:</b> {plugin.type}</div>
      <div><b>Accepting file:</b> {plugin.acceptingFile ? 'Yes' : 'No'}</div>
      <div className="plugin-action-row">
        {/* Execute or Send file & Execute Button */}
        {plugin.acceptingFile ? (
          <>
            <button
              className={`btn${!plugin.acceptingFile ? ' plugin-btn-disabled' : ''}`}
              onClick={() => fileInputRef.current && fileInputRef.current.click()}
              disabled={loading || !plugin.acceptingFile}
            >
              {loading && plugin.acceptingFile ? 'Sending...' : 'Send file & Execute'}
            </button>
            <input
              type="file"
              style={{ display: 'none' }}
              ref={fileInputRef}
              onChange={handleFileChange}
            />
          </>
        ) : (
          <button
            className={`btn${!plugin.executable ? ' plugin-btn-disabled' : ''}`}
            onClick={handleExecute}
            disabled={loading || !plugin.executable}
          >
            {loading && plugin.executable ? 'Executing...' : 'Execute'}
          </button>
        )}
        {/* View Result Button */}
        <Link
          to={`/plugin-result/${projectId}/${encodeURIComponent(plugin.name)}`}
          state={{ result: plugin.result }}
          className={`btn${(!plugin.viewable || !plugin.result) ? ' plugin-btn-disabled' : ''}`}
          style={{
            pointerEvents: (!plugin.viewable || !plugin.result) ? 'none' : 'auto',
            textDecoration: 'none',
            display: 'inline-block'
          }}
        >
          View Result
        </Link>
      </div>
      {/* Show message if result is not available */}
      {plugin.viewable && !plugin.result && (
        <div className="plugin-no-result">
          No result yet.
        </div>
      )}
      {error && <div className="error-message">{error}</div>}
    </li>
  );
};

export default Plugin;
