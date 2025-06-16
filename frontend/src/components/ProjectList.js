import React from 'react';
import Project from "./Project"

const ProjectList = ({ data }) => {
    return (
        <main className='main'>
            {data?.length === 0 && <div>No Projects. Please add a new project</div>}

            <ul className='project__list'>
                {data?.length > 0 && data.map(project => <Project project={project} key={project.id} />)}
            </ul>
        </main>
    )
}

export default ProjectList