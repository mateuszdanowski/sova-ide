import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {
  deleteProject,
  getProject,
} from '../api/ProjectService';

const ProjectDetail = () => {
  const [project, setProject] = useState({
    name: "",
  });

  const { id } = useParams();

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
    </>
  )
}

export default ProjectDetail;
