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

// export async function uploadFile(formData) {
//     return await axios.put(`${API_URL}/file`, formData);
// }

export async function deleteProject(id) {
    return await axios.delete(`${API_URL}/${id}`);
}
