import React, { useRef, useEffect } from 'react';
import * as d3 from 'd3';

/**
 * BarChart component using d3 to render a vertical bar chart.
 * @param {Object[]} data - Array of objects with 'label' and 'value'.
 */
const BarChart = ({ data }) => {
  // Transform Map<String, Object> to array of { label, value }
  const barData = Object.entries(data).map(([label, value]) => ({
    label,
    value: typeof value === 'number' ? value : Number(value)
  }));

  const svgRef = useRef();

  useEffect(() => {
    if (!Array.isArray(barData) || barData.length === 0) return;

    const width = 1000; // Chart width
    const height = Math.max(250, barData.length * 40); // Dynamic height for more bars
    const margin = { top: 20, right: 40, bottom: 40, left: 300 }; // Increased left margin for more label space
    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;

    d3.select(svgRef.current).selectAll('*').remove();

    const svg = d3.select(svgRef.current)
      .attr('width', width)
      .attr('height', height);

    // y scale for categories
    const y = d3.scaleBand()
      .domain(barData.map(d => d.label))
      .range([0, innerHeight])
      .padding(0.1);

    // x scale for values
    const x = d3.scaleLinear()
      .domain([0, d3.max(barData, d => d.value)])
      .nice()
      .range([0, innerWidth]);

    // Color scale from green (low) to red (high)
    const colorScale = d3.scaleLinear()
      .domain([0, d3.max(barData, d => d.value)])
      .range(["green", "red"]);

    const chart = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    // Bars (horizontal)
    chart.selectAll('.bar')
      .data(barData)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('y', d => y(d.label))
      .attr('x', 0)
      .attr('height', y.bandwidth())
      .attr('width', d => x(d.value))
      .attr('fill', d => colorScale(d.value));

    // y Axis (labels)
    chart.append('g')
      .call(d3.axisLeft(y));

    // x Axis (values)
    chart.append('g')
      .attr('transform', `translate(0,${innerHeight})`)
      .call(d3.axisBottom(x));

    // Value labels at end of bars
    chart.selectAll('.label')
      .data(barData)
      .enter()
      .append('text')
      .attr('class', 'label')
      .attr('x', d => x(d.value) + 5)
      .attr('y', d => y(d.label) + y.bandwidth() / 2)
      .attr('dy', '0.35em')
      .attr('text-anchor', 'start')
      .attr('font-size', '12px')
      .attr('fill', '#333')
      .text(d => d.value);
  }, [barData, data]);

  if (!Array.isArray(barData) || barData.length === 0) {
    return <div>No data to display.</div>;
  }

  return <svg ref={svgRef} style={{ background: '#f9f9f9' }} />;
};

export default BarChart;
