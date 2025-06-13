import React, {useEffect, useRef, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {
  deleteProject,
  getPackagesGraph,
  getProject,
  getProjectDetails,
} from '../api/ProjectService';
import * as d3 from 'd3';

const ProjectDetail = () => {
  const svgRef = useRef();
  const [data, setProjectDetails] = useState({
    links: [],
    nodes: [],
  });
  const svgRefPackages = useRef();
  const [packagesGraph, setPackagesGraph] = useState({
    links: [],
    nodes: [],
  });
  const [project, setProject] = useState({
    name: "",
  });

  const { id } = useParams();

  const fetchProjectDetails = async (id) => {
    try {
      const { data } = await getProjectDetails(id);
      setProjectDetails(data);
    } catch (error) {
      console.log(error);
    }
  }

  const fetchPackagesGraph = async (id) => {
    try {
      const { data } = await getPackagesGraph(id);
      setPackagesGraph(data);
    } catch (error) {
      console.log(error);
    }
  }

  const fetchProject = async (id) => {
    try {
      const { data } = await getProject(id);
      setProject(data);
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    fetchProject(id);
  }, [id]);

  useEffect(() => {
    fetchProjectDetails(id);
  }, [id]);

  useEffect(() => {
    fetchPackagesGraph(id);
  }, [id]);

  // classes graph
  useEffect(() => {
    if (!data.links.length > 0 || !data.nodes.length > 0) {
      return
    }
    // Specify the dimensions of the chart.
    const width = 928;
    const height = 680;

    // The force simulation mutates links and nodes, so create a copy
    const links = data.links.map(d => ({ ...d }));
    const nodes = data.nodes.map(d => ({ ...d }));

    const badness = d3.scaleLinear().domain([-1, 300]).range(["green", "red"]).clamp(true);

    const color = d => badness(d["metricLinesOfCode"]);

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
      .attr("stroke-width", d => Math.sqrt(2));

    const node = svg.append("g")
      .attr("stroke", "#fff")
      .attr("stroke-width", 1.5)
      .selectAll("circle")
      .data(nodes)
      .join("circle")
      .attr("r", (d) => 3 + Math.max(2, 2000.0 * d["pageRank"]))
      .attr("fill", color);

    node.append("title")
      .text(d => d.id);

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
  }, [data]);

  // packages graph
  useEffect(() => {
    if (!packagesGraph.links.length > 0 || !packagesGraph.nodes.length > 0) {
      return
    }

    // Specify the dimensions of the chart.
    const width = 928;
    const height = 680;

    // Specify the color scale.
    const color = d3.scaleOrdinal(d3.schemeCategory10);

    // The force simulation mutates links and nodes, so create a copy
    const links = packagesGraph.links.map(d => ({ ...d }));
    const nodes = packagesGraph.nodes.map(d => ({ ...d }));

    // Create a simulation with several forces.
    const simulation = d3.forceSimulation(nodes)
      .force("link", d3.forceLink(links).id(d => d.id))
      .force("charge", d3.forceManyBody())
      .force("x", d3.forceX())
      .force("y", d3.forceY());

    // Create the SVG container
    const svg = d3.select(svgRefPackages.current)
      .attr("width", width)
      .attr("height", height)
      .attr("viewBox", [-width / 2, -height / 2, width, height])
      .attr("style", "max-width: 100%; height: auto;");

    // Add a line for each link, and a circle for each node
    const link = svg.append("g")
      .selectAll("line")
      .data(links)
      .join("line")
      .attr('stroke', d => {
        return d.kind === 'in-package' ? 'red' : 'grey'
      })
      // .attr("stroke", "#999")
      .attr("stroke-opacity", 0.6)
      .attr("stroke-width", d => Math.sqrt(2));

    const node = svg.append("g")
      .attr("stroke", "#fff")
      .attr("stroke-width", 1.5)
      .selectAll("circle")
      .data(nodes)
      .join("circle")
      .attr("r", 5)
      .attr("fill", d => color(1));

    node.append("title")
      .text(d => d.id);

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
  }, [packagesGraph]);

  const handleDeleteProject = async (event) => {
    event.preventDefault();
    try {
      const { data } = await deleteProject(id);
      console.log(data);
      window.location.href = '/';
    } catch (error) {
      console.log(error);
    }
  }

  return (
    <>
      <Link to={'/'} className='link'><i className='bi bi-arrow-left'></i> Back to list</Link>
      <p>Viewing project <b>{project.name}</b></p>
      <button className='btn' onClick={handleDeleteProject}>Remove this project</button>
      <p>Class imports</p>
      <svg ref={svgRef}></svg>
      <p>Package imports and hierarchy</p>
      <svg ref={svgRefPackages}></svg>
    </>
  )
}

export default ProjectDetail;
