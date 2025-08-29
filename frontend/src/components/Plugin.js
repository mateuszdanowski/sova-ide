import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import '../components/ProjectDetail.css';
import '../components/Plugin.css';

const Plugin = ({ plugin, projectId, onExecuted }) => {
  const fileInputRef = useRef(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showPropertiesDialog, setShowPropertiesDialog] = useState(false);
  const [properties, setProperties] = useState({});

  const handleExecute = async () => {
    // Check if plugin has string inputs that require a dialog
    if (plugin.stringInputs && plugin.stringInputs.length > 0) {
      // Initialize properties object with empty values
      const initialProperties = {};
      plugin.stringInputs.forEach(input => {
        initialProperties[input] = '';
      });
      setProperties(initialProperties);
      setShowPropertiesDialog(true);
      return;
    }

    // If no properties needed, execute directly
    await executeWithProperties({});
  };

  const executeWithProperties = async (propertiesToSend) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `http://localhost:8080/projects/${projectId}/plugins/execute?pluginName=${encodeURIComponent(plugin.name)}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(propertiesToSend),
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

  const handlePropertiesSubmit = () => {
    // Check if all properties are filled
    const allFilled = Object.values(properties).every(value => value.trim() !== '');
    if (!allFilled) {
      setError('All properties must be filled');
      return;
    }

    setShowPropertiesDialog(false);
    setError(null);
    executeWithProperties(properties);
  };

  const handlePropertyChange = (propertyName, value) => {
    setProperties(prev => ({
      ...prev,
      [propertyName]: value
    }));
  };

  const handleDialogCancel = () => {
    setShowPropertiesDialog(false);
    setProperties({});
    setError(null);
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
              {loading && plugin.acceptingFile ? 'Executing...' : 'Send file & Execute'}
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

      {/* Properties Dialog */}
      {showPropertiesDialog && (
        <div className="properties-dialog">
          <div className="properties-dialog-content">
            <h3>Enter input properties for {plugin.name}</h3>
            {plugin.stringInputs && plugin.stringInputs.map((input, index) => (
              <div key={index} className="property-input-group">
                <label>{input}:</label>
                <input
                  type="text"
                  value={properties[input] || ''}
                  onChange={(e) => handlePropertyChange(input, e.target.value)}
                />
              </div>
            ))}
            {error && <div className="error-message">{error}</div>}
            <div className="properties-dialog-actions">
              <button onClick={handlePropertiesSubmit} className="btn">Submit</button>
              <button onClick={handleDialogCancel} className="btn btn-cancel">Cancel</button>
            </div>
          </div>
        </div>
      )}
    </li>
  );
};

export default Plugin;
