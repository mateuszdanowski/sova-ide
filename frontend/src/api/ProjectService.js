import axios from 'axios';

const API_URL = 'http://localhost:8080/projects';

export async function saveProject(project) {
    return await axios.post(API_URL, project);
}

export async function getProjects() {
    return await axios.get(`${API_URL}`);
}

export async function getProject(id) {
    return await axios.get(`${API_URL}/${id}`);
}

export async function getProjectPlugins(id) {
    return await axios.get(`${API_URL}/${id}/plugins`);
}

export async function executePlugin(id) {
    return await axios.post(`${API_URL}/${id}/plugins/execute`);
}

export async function executePluginWithFile(projectId, formData) {
    try {
      return await axios.post(
          `${API_URL}/${projectId}/plugins/execute-with-file`,
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        );
    } catch (error) {
        throw new Error(error.response?.data?.message || 'File execution failed');
    }
}

export async function executePluginWithProperties(projectId, pluginName, properties) {
    try {
      return await axios.post(
          `${API_URL}/${projectId}/plugins/execute?pluginName=${encodeURIComponent(pluginName)}`,
          properties,
          {
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );
    } catch (error) {
        throw new Error(error.response?.data?.message || 'Execution failed');
    }
}

export async function deleteProject(id) {
    return await axios.delete(`${API_URL}/${id}`);
}
