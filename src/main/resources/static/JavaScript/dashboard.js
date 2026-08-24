if(requireLogin()){
(async()=>{
  try{
    const [bR,iR,rR]=await Promise.all([
      apiFetch("/book/allbooks"),
      apiFetch(`/issued-books/user/${getUserId()}`),
      apiFetch(`/reservations/user/${getUserId()}`)
    ]);
    const books=bR?await readResponse(bR):[];
    const issued=iR?await readResponse(iR):[];
    const reservations=rR?await readResponse(rR):[];
    document.getElementById("bookCount").textContent=Array.isArray(books)?books.length:0;
    document.getElementById("issuedCount").textContent=Array.isArray(issued)?issued.filter(x=>x.status==="ISSUED"||x.status==="OVERDUE").length:0;
    document.getElementById("reservationCount").textContent=Array.isArray(reservations)?reservations.filter(x=>x.status==="PENDING").length:0;
    document.getElementById("welcomeName").textContent=getUsername()||"Reader";
    document.getElementById("recentBooks").innerHTML=(Array.isArray(books)?books.slice(0,6):[]).map(bookCard).join("")||'<div class="card empty">No books found.</div>';
  }catch(e){showToast("Could not load dashboard.","error");}
})();
}
