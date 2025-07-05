import {useEffect, useRef, useState} from "react";
import {getProjects, saveProject} from "./api/ProjectService";
import Header from "./components/Header";
import ProjectList from "./components/ProjectList";
import {Navigate, Route, Routes} from "react-router-dom";
import ProjectDetail from "./components/ProjectDetail";

function App() {
  const modalRef = useRef();
  // const fileRef = useRef();
  const [data, setData] = useState({});
  // const [file, setFile] = useState(undefined);
  const [values, setValues] = useState({
    name: '',
    // language: '',
    // status: '',
    // noOfClasses: 0,
    // noOfImports: 0,
    // avgLinesOfCode: 0,
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
      // const formData = new FormData();
      // formData.append('file', file, file.name);
      // formData.append('id', data.id);
      // const fileUrl = await updateFile(formData);
      // console.log(fileUrl)
      toggleModal(false);
      // setFile(undefined);
      // fileRef.current.value = null;
      setValues({
        name: '',
        // status: '',
        // noOfClasses: 0,
        // noOfImports: 0,
        // avgLinesOfCode: 0,
      })
      getAllProjects();
    } catch (error) {
      console.log(error);
    }
  }

  // const updateFile = async (formData) => {
  //   try {
  //     const { data: fileUrl } = await uploadFile(formData);
  //     return fileUrl;
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }

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
              {/*<div className="file-input">*/}
              {/*  <span className="details">Project File</span>*/}
              {/*  <input type="file" onChange={(event) => setFile(event.target.files[0])} ref={fileRef} name='file' required />*/}
              {/*</div>*/}
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
