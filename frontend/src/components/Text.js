import React, { useMemo } from 'react';

const Text = ({ data, config }) => {
  const content = useMemo(() => {
    if (!data) {
      return <div className="text-component-empty">No content provided</div>;
    }

    if (data.text) {
      return (
        <div
          className="text-content"
          style={{
            whiteSpace: 'pre-wrap',
            ...config?.style || {}
          }}
        >
          {data.text}
        </div>
      );
    }

    return (
      <div className="no-text-component">
        <pre>{JSON.stringify(data, null, 2)}</pre>
      </div>
    );
  }, [data, config]);

  return (
    <div
      className="text-component-wrapper"
      style={{
        padding: config?.padding || '10px',
        border: config?.border || 'none',
        borderRadius: config?.borderRadius || '0',
        backgroundColor: config?.backgroundColor || 'transparent',
        minHeight: config?.minHeight || 'auto',
        maxHeight: config?.maxHeight || 'none',
        overflow: config?.overflow || 'visible',
        ...config?.containerStyle
      }}
    >
      {content}
    </div>
  );
};

export default Text;
