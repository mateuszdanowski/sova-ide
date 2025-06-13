import React from 'react'
import { Link } from 'react-router-dom'

const Project = ({ project }) => {
    return (
        <Link to={`/projects/${project.id}`} className="project__item">
            <div className="project__header">
                <div className="project__file">
                    <p className="project_name">{project.name} </p>
                </div>
                <div className="project__body">
                    <p><i className="bi bi-info"></i> Number of classes: {project.noOfClasses} </p>
                    <p><i className="bi bi-info"></i> Number of imports: {project.noOfImports} </p>
                    <p><i className="bi bi-info"></i> AVG number of lines: {project.avgLinesOfCode} </p>
                    <p>{project.status === 'ANALYZED' ? <i className="bi bi-check-circle"></i> :
                        <i className="bi bi-x-circle"></i>} {project.status} </p>
                </div>
            </div>
        </Link>
    )
}

export default Project