import scripts.libs.Clipboard

def result = [:]
def site = params.site
def session = request.session

def clipboardItem = Clipboard.getItem(site, session)

result.site = site
if (clipboardItem != null) {
    result.item = clipboardItem.item
}

return result