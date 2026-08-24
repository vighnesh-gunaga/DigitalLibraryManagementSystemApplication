if(requireAdmin()){
const table=document.getElementById("bookTable");
const modal=document.getElementById("bookModal");
const form=document.getElementById("bookForm");
let editingId=null;

function openModal(book=null){
  editingId=book?.id||null;
  document.getElementById("modalTitle").textContent=editingId?"Edit Book":"Add Book";
  document.getElementById("title").value=book?.title||"";
  document.getElementById("author").value=book?.author||"";
  document.getElementById("isbn").value=book?.isbn||"";
  document.getElementById("categoryId").value=book?.categoryId||"";
  document.getElementById("publisherId").value=book?.publisherId||"";
  document.getElementById("quantity").value=book?.quantity??1;
  modal.classList.add("open");
}
function closeModal(){modal.classList.remove("open");form.reset();editingId=null;}
document.getElementById("addBookBtn").onclick=()=>openModal();
document.getElementById("closeModal").onclick=closeModal;
modal.addEventListener("click",e=>{if(e.target===modal)closeModal();});

async function loadOptions(){
  const [cr,pr]=await Promise.all([apiFetch("/category/getallcategory"),apiFetch("/publisher/allpublisher")]);
  if(cr){const c=await readResponse(cr);document.getElementById("categoryId").innerHTML='<option value="">Select category</option>'+(Array.isArray(c)?c:[]).map(x=>`<option value="${x.id}">${escapeHtml(x.name)}</option>`).join("");}
  if(pr){const p=await readResponse(pr);document.getElementById("publisherId").innerHTML='<option value="">Select publisher</option>'+(Array.isArray(p)?p:[]).map(x=>`<option value="${x.id}">${escapeHtml(x.name)}</option>`).join("");}
}
async function loadBooks(){
  const r=await apiFetch("/book/allbooks");if(!r)return;
  const data=await readResponse(r);
  if(!r.ok){table.innerHTML='<tr><td colspan="7" class="empty">Could not load books.</td></tr>';return;}
  table.innerHTML=(Array.isArray(data)?data:[]).map(x=>`<tr>
    <td>${x.id}</td><td><strong>${escapeHtml(x.title)}</strong></td><td>${escapeHtml(x.author)}</td>
    <td>${escapeHtml(x.categoryName||"—")}</td><td>${escapeHtml(x.publisherName||"—")}</td>
    <td>${x.availableQuantity??0}/${x.quantity??0}</td>
    <td><button class="btn btn-outline edit-btn" data-id="${x.id}">Edit</button>
        <button class="btn btn-danger delete-btn" data-id="${x.id}">Delete</button></td>
  </tr>`).join("")||'<tr><td colspan="7" class="empty">No books found.</td></tr>';
  document.querySelectorAll(".edit-btn").forEach(b=>b.onclick=()=>editBook(b.dataset.id));
  document.querySelectorAll(".delete-btn").forEach(b=>b.onclick=()=>deleteBook(b.dataset.id));
}
async function editBook(id){
  const r=await apiFetch(`/book/book/${id}`);if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast("Could not load book.","error");return;}
  openModal(data);
}
async function deleteBook(id){
  if(!confirm("Delete this book?"))return;
  const r=await apiFetch(`/book/deletebook/${id}`,{method:"DELETE"});if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast(typeof data==="string"?data:"Delete failed.","error");return;}
  showToast("Book deleted.","success");loadBooks();
}
form.addEventListener("submit",async e=>{
  e.preventDefault();
  const body={
    title:document.getElementById("title").value.trim(),
    author:document.getElementById("author").value.trim(),
    isbn:document.getElementById("isbn").value.trim(),
    categoryId:Number(document.getElementById("categoryId").value),
    publisherId:Number(document.getElementById("publisherId").value),
    quantity:Number(document.getElementById("quantity").value)
  };
  if(!body.title||!body.author||!body.categoryId||!body.publisherId||body.quantity<1){showToast("Fill all required fields.","error");return;}
  const path=editingId?`/book/updatebook/${editingId}`:"/book/addbook";
  const method=editingId?"PUT":"POST";
  const r=await apiFetch(path,{method,body:JSON.stringify(body)});if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast(typeof data==="string"?data:(data?.message||"Save failed"),"error");return;}
  showToast(editingId?"Book updated.":"Book added.","success");closeModal();loadBooks();
});
loadOptions();loadBooks();
}
