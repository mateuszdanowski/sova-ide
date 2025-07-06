import React from 'react';
import { useParams, useLocation } from 'react-router-dom';
import componentRegistry from './ComponentRegistry';

const PluginResult = () => {
  const { pluginName } = useParams();
  const location = useLocation();
  const result = location.state?.result;

  if (!result || !result.guiComponentData) return <div>No result available.</div>;

  const Comp = componentRegistry[result.guiComponentData.componentType];
  if (!Comp) return <div style={{ color: 'red' }}>Unknown component type: {result.guiComponentData.componentType}</div>;

  return (
    <div style={{ margin: 24 }}>
      <h2>Plugin Result: {pluginName}</h2>
      <button
        className="btn"
        onClick={() => window.history.back()}
        style={{ marginBottom: 16 }}
      >
        Back to Project
      </button>
      <Comp data={result.guiComponentData.data} config={result.guiComponentData.config} />
    </div>
  );
};

export default PluginResult;
