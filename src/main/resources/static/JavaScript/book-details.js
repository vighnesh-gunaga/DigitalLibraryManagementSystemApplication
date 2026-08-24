if(requireLogin()){
const id=new URLSearchParams(location.search).get("id");
const box=document.getElementById("bookDetails");

async function load(){
  if(!id){box.innerHTML='<div class="card empty">Book ID is missing.</div>';return;}
  const r=await apiFetch(`/book/book/${encodeURIComponent(id)}`);if(!r)return;
  const book=await readResponse(r);
  if(!r.ok){box.innerHTML='<div class="card empty">Book not found.</div>';return;}
  const available=Number(book.availableQuantity||0)>0;
  box.innerHTML=`<div class="card detail-card">
    <span class="badge">${escapeHtml(book.categoryName||"Uncategorized")}</span>
    <h1 class="detail-title">${escapeHtml(book.title)}</h1>
    <p class="muted">By ${escapeHtml(book.author)}</p>
    <div class="detail-meta">
      <div class="meta-item"><small>ISBN</small><strong>${escapeHtml(book.isbn||"—")}</strong></div>
      <div class="meta-item"><small>Publisher</small><strong>${escapeHtml(book.publisherName||"—")}</strong></div>
      <div class="meta-item"><small>Total Copies</small><strong>${book.quantity??0}</strong></div>
      <div class="meta-item"><small>Available</small><strong>${book.availableQuantity??0}</strong></div>
    </div>
    <div class="actions">
      <button id="issueBtn" class="btn btn-primary" ${available?"":"disabled"}>${available?"Issue Book":"Currently Unavailable"}</button>
      ${!available?'<button id="reserveBtn" class="btn btn-outline">Reserve Book</button>':""}
      <a class="btn btn-outline" href="/HTML/browse-books.html">Back</a>
    </div>
  </div>`;
  document.getElementById("issueBtn").onclick=()=>issueBook(book.id);
  document.getElementById("reserveBtn")?.addEventListener("click",()=>reserveBook(book.id));
}
async function issueBook(bookId){
  const r=await apiFetch("/issued-books/issue",{method:"POST",body:JSON.stringify({userId:Number(getUserId()),bookId:Number(bookId)})});
  if(!r)return;const data=await readResponse(r);
  if(!r.ok){showToast(typeof data==="string"?data:"Could not issue book.","error");return;}
  showToast("Book issued successfully.","success");setTimeout(load,600);
}
async function reserveBook(bookId){
  const r=await apiFetch("/reservations/create",{method:"POST",body:JSON.stringify({userId:Number(getUserId()),bookId:Number(bookId)})});
  if(!r)return;const data=await readResponse(r);
  if(!r.ok){showToast(typeof data==="string"?data:"Could not reserve book.","error");return;}
  showToast("Book reserved successfully.","success");setTimeout(()=>location.href="/HTML/reservations.html",600);
}
load();
}
