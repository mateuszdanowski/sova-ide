import React from 'react';
import Project from "./Project"

const ProjectList = ({ data, currentPage, getAllProjects }) => {
    return (
        <main className='main'>
            {data?.content?.length === 0 && <div>No Projects. Please add a new project</div>}

            <ul className='project__list'>
                {data?.content?.length > 0 && data.content.map(project => <Project project={project} key={project.id} />)}
            </ul>

            {data?.content?.length > 0 && data?.totalPages > 1 &&
                <div className='pagination'>
                    <a onClick={() => getAllProjects(currentPage - 1)} className={0 === currentPage ? 'disabled' : ''}>&laquo;</a>

                    { data && [...Array(data.totalPages).keys()].map((page, index) =>
                        <a onClick={() => getAllProjects(page)} className={currentPage === page ? 'active' : ''} key={page}>{page + 1}</a>)}


                    <a onClick={() => getAllProjects(currentPage + 1)} className={data.totalPages === currentPage + 1 ? 'disabled' : ''}>&raquo;</a>
                </div>
            }

        </main>
    )
}

export default ProjectList