import React, { useRef, useState } from 'react';
import '../components/ProjectDetail.css';

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
      {plugin.acceptingFile ? (
        <>
          <button
            className="btn"
            onClick={() => fileInputRef.current && fileInputRef.current.click()}
            disabled={loading}
          >
            {loading ? 'Sending...' : 'Send file & Execute'}
          </button>
          <input
            type="file"
            style={{ display: 'none' }}
            ref={fileInputRef}
            onChange={handleFileChange}
          />
        </>
      ) : (
        <button className="btn" onClick={handleExecute} disabled={loading}>
          {loading ? 'Executing...' : 'Execute'}
        </button>
      )}
      {error && <div className="error-message">{error}</div>}
    </li>
  );
};

export default Plugin;
