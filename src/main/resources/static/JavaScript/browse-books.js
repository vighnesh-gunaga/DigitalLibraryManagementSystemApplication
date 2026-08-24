if(requireLogin()){
const grid=document.getElementById("bookGrid");
const search=document.getElementById("search");
const mode=document.getElementById("searchMode");

async function loadBooks(path="/book/allbooks"){
  const r=await apiFetch(path);if(!r)return;
  const data=await readResponse(r);
  if(!r.ok){grid.innerHTML='<div class="card empty">Could not load books.</div>';return;}
  grid.innerHTML=(Array.isArray(data)?data:[]).map(bookCard).join("")||'<div class="card empty">No books found.</div>';
}

document.getElementById("searchForm").addEventListener("submit",async e=>{
  e.preventDefault();
  const q=search.value.trim();
  if(!q){loadBooks();return;}
  const endpoint=mode.value==="title"?"/book/search/title?title=":mode.value==="author"?"/book/search/author?author=":"/book/search/category?category=";
  loadBooks(endpoint+encodeURIComponent(q));
});
document.getElementById("clearSearch").onclick=()=>{search.value="";loadBooks();};
loadBooks();
}
