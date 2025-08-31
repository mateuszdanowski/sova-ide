import React from 'react';
import PropTypes from 'prop-types';

/**
 * Table component to display array of objects as a table.
 * @param {Object[]} data - Array of objects to display.
 */
const Table = ({ data }) => {
  // Expect data as { table: [...] }
  const tableData = data?.table;
  if (!Array.isArray(tableData) || tableData.length === 0) {
    return <div>No data to display.</div>;
  }

  // Get table headers from keys of first object
  const headers = Object.keys(tableData[0]);

  return (
    <table className="custom-table">
      <thead>
        <tr>
          {headers.map((header) => (
            <th key={header}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {tableData.map((row, idx) => (
          <tr key={idx}>
            {headers.map((header) => (
              <td key={header}>{row[header]}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

Table.propTypes = {
  data: PropTypes.shape({
    table: PropTypes.arrayOf(PropTypes.object).isRequired,
  }).isRequired,
};

export default Table;
