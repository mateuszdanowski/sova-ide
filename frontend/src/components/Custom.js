import React, { useMemo } from 'react';

const Custom = ({ data, config }) => {
  const content = useMemo(() => {
    if (!data) {
      return <div className="custom-component-empty">No content provided</div>;
    }

    if (data.type === 'html' && data.html) {
      return (
        <div
          className="custom-html-content"
          dangerouslySetInnerHTML={{ __html: data.html }}
          style={config?.style || {}}
        />
      );
    }

    if (data.type === 'text' && data.text) {
      return (
        <div className="custom-text-content" style={config?.style || {}}>
          {data.text}
        </div>
      );
    }

    return (
      <div className="custom-component-fallback">
        <pre>{JSON.stringify(data, null, 2)}</pre>
      </div>
    );
  }, [data, config]);

  return (
    <div
      className="custom-component-wrapper"
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

export default Custom;
