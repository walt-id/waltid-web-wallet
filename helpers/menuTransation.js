
export const menuTransitionShow = () =>{
    //Dom's variable
    let widget = document.getElementById('widget')
    let dash1 = document.getElementById('dash-1')
    let dash2 = document.getElementById('dash-2')
    let dash3 = document.getElementById('dash-3')
    let content = document.getElementById('content')
    let menuContent = document.getElementById('menu-content')
    let copyright = document.getElementById('copyright')
    //Dom's execute
    console.log('show menu')
    content.classList.add('hide')
    menuContent.classList.remove('hide')
    widget.classList.remove('bg-w')
    widget.classList.add('bg-g')
    widget.classList.add('_border')
    dash1.classList.add('_close-x')
    dash2.classList.add('hide')
    dash3.classList.add('_close-y')
    console.log('show menu')
    copyright.classList.remove('_blue-color')
    copyright.classList.add('_white-color')
}
export const menuTransitionHide = () =>{
    //Dom's variable
    let widget = document.getElementById('widget')
    let dash1 = document.getElementById('dash-1')
    let dash2 = document.getElementById('dash-2')
    let dash3 = document.getElementById('dash-3')
    let content = document.getElementById('content')
    let menuContent = document.getElementById('menu-content')
    let copyright = document.getElementById('copyright')
    //Dom's execute
    content.classList.remove('hide')
    menuContent.classList.add('hide')
    widget.classList.add('bg-w')
    widget.classList.remove('bg-g')
    widget.classList.remove('_border')
    dash1.classList.remove('_close-x')
    dash2.classList.remove('hide')
    dash3.classList.remove('_close-y')
    copyright.classList.add('_blue-color')
    copyright.classList.remove('_white-color')
}
