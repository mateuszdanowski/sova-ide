import {useEffect, useRef, useState} from "react";
import {getProjects, saveProject} from "./api/ProjectService";
import Header from "./components/Header";
import ProjectList from "./components/ProjectList";
import {Navigate, Route, Routes} from "react-router-dom";
import ProjectDetail from "./components/ProjectDetail";
import PluginResult from "./components/PluginResult";

function App() {
  const modalRef = useRef();
  const [data, setData] = useState({});
  const [values, setValues] = useState({
    name: '',
  });


  const getAllProjects = async () => {
    try {
      const { data } = await getProjects();
      setData(data);
      console.log(data);
    } catch (error) {
      console.log(error);
    }
  }

  const onChange = (event) => {
    setValues({ ...values, [event.target.name]: event.target.value });
  }

  const handleNewProject = async (event) => {
    event.preventDefault();
    try {
      const { data } = await saveProject(values);
      console.log(data);
      toggleModal(false);
      setValues({
        name: '',
      })
      getAllProjects();
    } catch (error) {
      console.log(error);
    }
  }

  const toggleModal = (show) => show ? modalRef.current.showModal() : modalRef.current.close();

  useEffect(() => {
    getAllProjects();
  }, []);

  return (
    <>
      <Header toggleModal={toggleModal} nbOfProjects={data.length} />
      <main className='main'>
        <div className='container'>
          <Routes>
            <Route path ='/' element={<Navigate to={'/projects'}/>} />
            <Route path="/projects" element={<ProjectList data={data} getAllProjects={getAllProjects}/>}/>
            <Route path="/projects/:id" element={<ProjectDetail />}/>
            <Route path="/plugin-result/:projectId/:pluginName" element={<PluginResult />} />
          </Routes>
        </div>
      </main>

      {/* Modal */}
      <dialog ref={modalRef} className="modal" id="modal">
        <div className="modal__header">
          <h3>New Project</h3>
          <i onClick={() => toggleModal(false)} className="bi bi-x-lg"></i>
        </div>
        <div className="divider"></div>
        <div className="modal__body">
          <form onSubmit={handleNewProject}>
            <div className="user-details">
              <div className="input-box">
                <span className="details">Name</span>
                <input type="text" value={values.name} onChange={onChange} name='name' required />
              </div>
            </div>
            <div className="form_footer">
              <button onClick={() => toggleModal(false)} type='button' className="btn btn-danger">Cancel</button>
              <button type='submit' className="btn">Add</button>
            </div>
          </form>
        </div>
      </dialog>
    </>
  );
}

export default App;
