if(requireAdmin()){
const table=document.getElementById("reservationTable");
async function load(){
  const r=await apiFetch("/reservations/all");if(!r)return;
  const data=await readResponse(r);
  if(!r.ok){table.innerHTML='<tr><td colspan="6" class="empty">Could not load reservations.</td></tr>';return;}
  table.innerHTML=(Array.isArray(data)?data:[]).map(x=>`<tr>
    <td>${x.id}</td><td>${escapeHtml(x.username)}</td><td>${escapeHtml(x.bookTitle)}</td>
    <td>${formatDate(x.reservationDate)}</td>
    <td><span class="status ${escapeHtml(x.status)}">${escapeHtml(x.status)}</span></td>
    <td>${x.status==="PENDING"?`
      <button class="btn btn-success fulfill-btn" data-id="${x.id}">Fulfill</button>
      <button class="btn btn-danger cancel-btn" data-id="${x.id}">Cancel</button>`:"—"}</td>
  </tr>`).join("")||'<tr><td colspan="6" class="empty">No reservations found.</td></tr>';
  document.querySelectorAll(".fulfill-btn").forEach(b=>b.onclick=()=>updateReservation(b.dataset.id,"fulfill"));
  document.querySelectorAll(".cancel-btn").forEach(b=>b.onclick=()=>updateReservation(b.dataset.id,"cancel"));
}
async function updateReservation(id,action){
  if(!confirm(`${action==="fulfill"?"Fulfill":"Cancel"} this reservation?`))return;
  const r=await apiFetch(`/reservations/${action}/${id}`,{method:"PUT"});if(!r)return;
  const data=await readResponse(r);if(!r.ok){showToast(typeof data==="string"?data:"Operation failed.","error");return;}
  showToast(`Reservation ${action}ed successfully.`,"success");load();
}
document.getElementById("refreshBtn").onclick=load;
load();
}
