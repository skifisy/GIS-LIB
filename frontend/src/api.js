import axios from 'axios'

// allow configuring the API base URL via Vite env var VITE_API_BASE
// e.g. VITE_API_BASE=http://localhost:8080/api
const baseURL = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE)
  ? import.meta.env.VITE_API_BASE
  : 'http://localhost:8080/api'

const api = axios.create({ baseURL })

export async function listLibraries() {
  const res = await api.get('/libraries')
  return res.data
}

export async function createLibrary(lib) {
  const res = await api.post('/libraries', lib)
  return res.data
}

export async function deleteLibrary(id) {
  const res = await api.delete(`/libraries/${id}`)
  return res.data
}

export async function updateLibrary(id, lib) {
  const res = await api.put(`/libraries/${id}`, lib)
  return res.data
}

export async function listBooks(libraryId) {
  const res = await api.get('/books', { params: { libraryId } })
  return res.data
}

// safe helper: fetch all books without sending undefined params
export async function listAllBooks() {
  const res = await api.get('/books')
  return res.data
}

export async function createBook(book) {
  const res = await api.post('/books', book)
  return res.data
}

export async function deleteBook(id) {
  const res = await api.delete(`/books/${id}`)
  return res.data
}

export async function updateBook(id, book) {
  const res = await api.put(`/books/${id}`, book)
  return res.data
}

export async function listRents(studentId) {
  const res = await api.get('/rents', { params: { studentId } })
  return res.data
}

// safe helper: fetch all rents without sending undefined params
export async function listAllRents() {
  const res = await api.get('/rents')
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

export async function deleteRent(id) {
  const res = await api.delete(`/rents/${id}`)
  return res.data
}

export async function updateRent(id, rent) {
  const res = await api.put(`/rents/${id}`, rent)
  return res.data
}

export default api
