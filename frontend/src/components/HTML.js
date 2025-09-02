import React, { useMemo } from 'react';
import DOMPurify from 'dompurify';

const HTML = ({ data, config }) => {
  const content = useMemo(() => {
    if (!data) {
      return <div className="html-component-empty">No content provided</div>;
    }

    // If data.html exists, sanitize it before rendering
    if (data.html) {
      return (
        <div
          className="custom-html-content"
          dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(data.html) }}
          style={config?.style || {}}
        />
      );
    }

    // If data is a string, sanitize and render
    if (typeof data === 'string') {
      return (
        <div
          className="custom-html-content"
          dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(data) }}
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
