import React from 'react'
import { Link } from 'react-router-dom'

const Project = ({ project }) => {
    return (
        <Link to={`/projects/${project.id}`} className="project__item">
            <div className="project__header">
                <div className="project__file">
                    <p className="project_name">{project.name} </p>
                </div>
            </div>
        </Link>
    )
}

export default Project