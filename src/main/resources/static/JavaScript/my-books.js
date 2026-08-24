if(requireLogin()){
async function load(){
  const [ir,fr]=await Promise.all([apiFetch(`/issued-books/user/${getUserId()}`),apiFetch(`/fines/user/${getUserId()}`)]);
  if(!ir)return;
  const issued=await readResponse(ir)||[], fines=fr?await readResponse(fr)||[]:[];
  document.getElementById("issuedTotal").textContent=issued.filter(x=>x.status==="ISSUED"||x.status==="OVERDUE").length;
  document.getElementById("overdueTotal").textContent=issued.filter(x=>x.status==="OVERDUE").length;
  document.getElementById("fineTotal").textContent="₹"+fines.filter(x=>!x.paid).reduce((s,x)=>s+Number(x.amount||0),0).toFixed(2);
  document.getElementById("issuedTable").innerHTML=(Array.isArray(issued)?issued:[]).map(x=>`<tr>
    <td><strong>${escapeHtml(x.bookTitle)}</strong><br><small>ID: ${x.bookId}</small></td>
    <td>${formatDate(x.issueDate)}</td><td>${formatDate(x.dueDate)}</td>
    <td><span class="status ${escapeHtml(x.status)}">${escapeHtml(x.status)}</span></td>
    <td>${x.status!=="RETURNED"?`<button class="btn btn-outline return-btn" data-id="${x.id}">Return</button>`:"—"}</td>
  </tr>`).join("")||'<tr><td colspan="5" class="empty">No issued books.</td></tr>';
  document.querySelectorAll(".return-btn").forEach(b=>b.onclick=()=>returnBook(b.dataset.id));
  document.getElementById("fineTable").innerHTML=(Array.isArray(fines)?fines:[]).map(x=>`<tr>
    <td>${escapeHtml(x.bookTitle)}</td><td>${formatDate(x.dueDate)}</td><td>${x.overdueDays}</td>
    <td>₹${Number(x.amount||0).toFixed(2)}</td><td><span class="status ${x.paid?"PAID":"OVERDUE"}">${x.paid?"PAID":"UNPAID"}</span></td>
  </tr>`).join("")||'<tr><td colspan="5" class="empty">No fines found.</td></tr>';
}
async function returnBook(id){
  if(!confirm("Return this book?"))return;
  const r=await apiFetch(`/issued-books/return/${id}`,{method:"PUT"});if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast(typeof data==="string"?data:"Return failed.","error");return;}
  showToast("Book returned successfully.","success");load();
}
load();
}
