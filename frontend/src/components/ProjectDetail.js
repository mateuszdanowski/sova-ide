import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {
  deleteProject,
  getProject,
  getProjectPlugins,
} from '../api/ProjectService';
import './ProjectDetail.css';
import Plugin from './Plugin';

const ProjectDetail = () => {
  const [project, setProject] = useState({
    name: "",
  });
  const [plugins, setPlugins] = useState([]);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const { id } = useParams();

  const fetchProject = async (id) => {
    try {
      const { data } = await getProject(id);
      setProject(data);
    } catch (error) {
      console.log(error);
    }
  }

  const fetchPlugins = async (id) => {
    try {
      const { data } = await getProjectPlugins(id);
      console.log(data);
      setPlugins(data);
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    fetchProject(id);
    fetchPlugins(id);
  }, [id]);

  const handleDeleteProject = (event) => {
    event.preventDefault();
    setShowDeleteDialog(true);
  }

  const confirmDeleteProject = async () => {
    try {
      const { data } = await deleteProject(id);
      console.log(data);
      window.location.href = '/';
    } catch (error) {
      console.log(error);
    }
  }

  const cancelDeleteProject = () => {
    setShowDeleteDialog(false);
  }

  const handlePluginExecuted = () => {
    // Refresh plugins data after execution to update the View Results button state
    fetchPlugins(id);
  }

  return (
    <>
      <Link to={'/'} className='link'><i className='bi bi-arrow-left'></i> Back to list</Link>
      <p>Viewing project <b>{project.name}</b></p>
      <button className='btn' onClick={handleDeleteProject}>Remove this project</button>

      <div className="project-detail-container">
        <div className="plugin-column">
          <h3>Input plugins</h3>
          <ul className='plugin-list'>
            {plugins?.length > 0 && plugins.filter(p => p.type === 'INPUT')
              .map(plugin => (
                <Plugin key={plugin.name} plugin={plugin} projectId={id} onExecuted={handlePluginExecuted} />
              ))}
          </ul>
        </div>
        <div className="plugin-column">
          <h3>Output plugins</h3>
          <ul className="plugin-list">
            {plugins?.length > 0 && plugins.filter(p => p.type === 'OUTPUT')
              .map(plugin => (
                <Plugin key={plugin.name} plugin={plugin} projectId={id} onExecuted={handlePluginExecuted} />
              ))}
          </ul>
        </div>
      </div>

      {/* Delete Confirmation Dialog */}
      {showDeleteDialog && (
        <div className="confirmation-dialog">
          <div className="confirmation-dialog-content">
            <h3>Confirm deletion</h3>
            <p>Are you sure you want to delete the project <strong>{project.name}</strong>?</p>
            <p>This action cannot be undone.</p>
            <div className="confirmation-dialog-actions">
              <button onClick={confirmDeleteProject} className="btn btn-danger">Delete</button>
              <button onClick={cancelDeleteProject} className="btn btn-cancel">Cancel</button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default ProjectDetail;
