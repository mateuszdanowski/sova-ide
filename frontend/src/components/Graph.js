import React, {useEffect, useRef} from 'react';
import * as d3 from 'd3';

const Graph = ({ data, config }) => {
  const svgRef = useRef();

  useEffect(() => {
    if (!data.links.length > 0 || !data.nodes.length > 0) {
      return
    }
    const width = config.width || 928;
    const height = config.height || 680;
    const nodeRadius = config.nodeRadius || 5;
    const linkStrength = config.linkStrength || Math.sqrt(2);

    const links = data.links.map(d => ({ ...d }));
    const nodes = data.nodes.map(d => ({ ...d }));

    // Create a simulation with several forces.
    const simulation = d3.forceSimulation(nodes)
      .force("link", d3.forceLink(links).id(d => d.id))
      .force("charge", d3.forceManyBody())
      .force("x", d3.forceX())
      .force("y", d3.forceY());

    // Create the SVG container
    const svg = d3.select(svgRef.current)
      .attr("width", width)
      .attr("height", height)
      .attr("viewBox", [-width / 2, -height / 2, width, height])
      .attr("style", "max-width: 100%; height: auto;");

    // Add a line for each link, and a circle for each node
    const link = svg.append("g")
      .attr("stroke", "#999")
      .attr("stroke-opacity", 0.6)
      .selectAll("line")
      .data(links)
      .join("line")
      .attr("stroke-width", linkStrength);

    const badness = d3.scaleLinear().domain([-1, 300]).range(["green", "red"]).clamp(true);
    const node = svg.append("g")
      .attr("stroke", "#fff")
      .attr("stroke-width", 1.5)
      .selectAll("circle")
      .data(nodes)
      .join("circle")
      .attr("r", (d) => d["size"] !== undefined ? 3 + Math.max(2, 10 * d["size"]) : nodeRadius)
      .attr("fill", (d) => d["color"] !== undefined ? badness(d["color"]) : d3.scaleOrdinal(d3.schemeCategory10)(1));

    node.append("title")
      .text(d => d.name);

    // Add a drag behavior
    node.call(d3.drag()
      .on("start", dragstarted)
      .on("drag", dragged)
      .on("end", dragended));

    // Set the position attributes of links and nodes each time the simulation ticks
    simulation.on("tick", () => {
      link
        .attr("x1", d => d.source.x)
        .attr("y1", d => d.source.y)
        .attr("x2", d => d.target.x)
        .attr("y2", d => d.target.y);

      node
        .attr("cx", d => d.x)
        .attr("cy", d => d.y);
    });

    // Reheat the simulation when drag starts, and fix the subject position
    function dragstarted(event) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      event.subject.fx = event.subject.x;
      event.subject.fy = event.subject.y;
    }

    // Update the subject (dragged node) position during drag
    function dragged(event) {
      event.subject.fx = event.x;
      event.subject.fy = event.y;
    }

    // Restore the target alpha so the simulation cools after dragging ends
    function dragended(event) {
      if (!event.active) simulation.alphaTarget(0);
      event.subject.fx = null;
      event.subject.fy = null;
    }
    // Cleanup function to stop simulation when the component is unmounted
    return () => simulation.stop();
  }, [data, config]);

  return (
    <div style={{ border: '1px solid #ccc', padding: 16, marginTop: 16 }}>
      <h4>Graph Visualization</h4>
      <svg ref={svgRef}></svg>
    </div>
  );
};

export default Graph;

