import axios from 'axios'

const api = axios.create({ baseURL: 'http://localhost:8080/api' })

export async function listLibraries() {
  const res = await api.get('/libraries')
  return res.data
}

export async function createLibrary(lib) {
  const res = await api.post('/libraries', lib)
  return res.data
}

export async function listBooks(libraryId) {
  const res = await api.get('/books', { params: { libraryId } })
  return res.data
}

export async function createBook(book) {
  const res = await api.post('/books', book)
  return res.data
}

export async function listRents(studentId) {
  const res = await api.get('/rents', { params: { studentId } })
  return res.data
}

export async function borrow(bookId, studentId) {
  const res = await api.post('/borrow', { bookId, studentId })
  return res.data
}

export async function returnBook(rentId, studentId) {
  const res = await api.post('/return', { rentId, studentId })
  return res.data
}

export default api
