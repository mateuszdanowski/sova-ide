import React, { useMemo } from 'react';

const HTML = ({ data, config }) => {
  const content = useMemo(() => {
    if (!data) {
      return <div className="html-component-empty">No content provided</div>;
    }

    if (data.html) {
      return (
        <div
          className="custom-html-content"
          dangerouslySetInnerHTML={{ __html: data.html }}
          style={config?.style || {}}
        />
      );
    }

    return (
      <div className="no-html-component">
        <pre>{JSON.stringify(data, null, 2)}</pre>
      </div>
    );
  }, [data, config]);

  return (
    <div
      className="html-component-wrapper"
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

export default HTML;
