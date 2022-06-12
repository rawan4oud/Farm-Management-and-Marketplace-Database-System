//alert("AFA");
alert(products_quantities.length);
var abc =products_quantities.length;
//alert(products_quantities[7]);




var div;
for (var i = 1; i < abc; i++) {
   // alert(parseInt(product_quantities[i]));
    if (products_quantities[i]>0){
        alert("lol");
        div = document.getElementById(parseInt(i));
        div.parentNode.insertBefore(div, document.getElementById('0'));
    }

}


