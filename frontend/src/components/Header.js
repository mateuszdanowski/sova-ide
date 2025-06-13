import React from 'react'

const Header = ({ toggleModal, nbOfProjects }) => {
    return (
        <header className='header'>
            <div className='container'>
                <h3>Project List ({nbOfProjects})</h3>
                <button onClick={() => toggleModal(true)} className='btn'>
                    <i className='bi bi-plus-square'></i> Add New Project
                </button>
            </div>
        </header>
    )
}

export default Header