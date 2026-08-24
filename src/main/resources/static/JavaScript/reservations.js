if(requireLogin()){
async function load(){
  const r=await apiFetch(`/reservations/user/${getUserId()}`);if(!r)return;
  const data=await readResponse(r);
  if(!r.ok){document.getElementById("reservationTable").innerHTML='<tr><td colspan="4" class="empty">Could not load reservations.</td></tr>';return;}
  document.getElementById("reservationTable").innerHTML=(Array.isArray(data)?data:[]).map(x=>`<tr>
    <td><strong>${escapeHtml(x.bookTitle)}</strong><br><small>ID: ${x.bookId}</small></td>
    <td>${formatDate(x.reservationDate)}</td>
    <td><span class="status ${escapeHtml(x.status)}">${escapeHtml(x.status)}</span></td>
    <td>${x.status==="PENDING"?`<button class="btn btn-danger cancel-btn" data-id="${x.id}">Cancel</button>`:"—"}</td>
  </tr>`).join("")||'<tr><td colspan="4" class="empty">No reservations yet.</td></tr>';
  document.querySelectorAll(".cancel-btn").forEach(b=>b.onclick=()=>cancelReservation(b.dataset.id));
}
async function cancelReservation(id){
  if(!confirm("Cancel this reservation?"))return;
  const r=await apiFetch(`/reservations/cancel/${id}`,{method:"PUT"});if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast(typeof data==="string"?data:"Could not cancel.","error");return;}
  showToast("Reservation cancelled.","success");load();
}
load();
}
