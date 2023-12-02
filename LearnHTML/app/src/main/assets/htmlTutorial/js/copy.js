  function copy(that){
    var inp = document.createElement('input');
    document.body.appendChild(inp)
    inp.value = that.innerText;
    inp.select();
    document.execCommand('copy',false);
    inp.remove();
  }
  
  function copyHTML(that){
    var inp = document.createElement('input');
    document.body.appendChild(inp)
    inp.value = that.innerHTML;
    inp.select();
    document.execCommand('copy',false);
    inp.remove();
  }

  function copyText(that){
    var inp = document.createElement('input');
    document.body.appendChild(inp)
    inp.value = that;
    inp.select();
    document.execCommand('copy',false);
    inp.remove();
  }

  if (document.querySelectorAll(".codehemu-clear")[1]) {
    document.querySelectorAll(".codehemu-clear")[1].style.marginBottom="40px";
  }


