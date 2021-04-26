let user = null
let lastPage = []
let firstPage = "search"
let token = null
let url = "https://lacueillette.ml/"
let api = "https://lacueillette.ml/api/"

let city = "Rennes"
let postalcode = 35000

$(()=>{
    setInterval(renewToken, 3600000)

    openWindow(firstPage)

    window.onpopstate = e=>{
        let w = e.state.window
        closeWindows()
        $(".window."+w).css("display", "grid")
    }

    $("header .account").click(()=>{
        if(user === null)
            openWindow("login")
        else {
            openWindow("user")
            userAct()
        }
    })
    $("header .logo").click(()=>{
        window.location.replace(url)
    })
    $("header .address").click(()=>{
        openWindow("select")
        $(".window.select .postalcode input").val(postalcode)
        $(".window.select .city input").val(city)
    })
    $(".window.select form").submit(e=>e.preventDefault())
    $(".window.select .submit").click(()=>{
        let pc = $(".window.select .postalcode input").val()
        let city_ = $(".window.select .city input").val()

        clearInputError()

        if(pc === "")
            inputError("postalcode")
        else if(city_ === "")
            inputError("city")
        else{
            postalcode = pc
            city = city_
            $("header .address span").html(city)
            openWindow("search")
        }
    })

    checkAlive()

})

function renewToken(){
    if(user === null)
        return;
    $.post(api + "user/newToken", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            token = data.value
            document.cookie = "token="+data.value + "; expires="+dateToString(data.expiration)
        }
    }, "json")
}

function checkAlive(){
    $.post(api + "alive", {}, data=>{
        if(!data.log || data.log !== "alive") {
            errorPopup("Erreur de communication avec le serveur")
            setTimeout(checkAlive, 6000)
        }else{
            setTimeout(checkAlive, 30000)
        }
    }).fail(()=>{
        errorPopup("Communication impossible avec le serveur")
        setTimeout(checkAlive, 6000)
    })
}

function openWindow(name){
    closeWindows()
    $(".window."+name + " input").val("")
    clearInputError()
    $(".window."+name).css("display", "grid")
    if(name !== "login" && name !== "register") {
        lastPage.push(name)
        history.pushState({window:name},name)
    }
}
function closeWindows(){
    $(".window").css("display", "none")
}
function inputError(name){
    $(".input."+name).addClass("error")
    $(".input."+name + " input").focus()
}
function clearInputError(){
    $(".input").removeClass("error")
}
function errorPopup(msg){
    console.log(msg)

    let pop = $(".errorPopup")

    pop.html(msg)
    pop.css("display", "block")

    setTimeout(()=>{
        pop.css("display", "none")
    }, 5000)
}
function successPopup(msg){
    console.log(msg)

    let pop = $(".successPopup")

    pop.html(msg)
    pop.css("display", "block")

    setTimeout(()=>{
        pop.css("display", "none")
    }, 5000)
}
function dateToString(date){
    return (new Date(date.year, date.month, date.day+1, date.hour, date.minute, date.second)).toString()
}
function numFormat(num, n){
    num = num.toString()

    while(num.length < n)
        num = "0" + num

    return num
}
function dateToStringFormat(date){
    return numFormat(date.day, 2) + "-" + numFormat(date.month, 2) + "-" +numFormat(date.year, 4) + " " + numFormat(date.hour, 2) + ":" + numFormat(date.minute, 2) + ":" + numFormat(date.second, 2)
}