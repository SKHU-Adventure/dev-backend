import lightning.pytorch as pl
from backbones import get_backbone
from models import get_model

class EmbedNet(pl.LightningModule):
    def __init__(self, backbone, model):
        super(EmbedNet, self).__init__()
        self.backbone = backbone
        self.model = model

    def forward(self, x):
        x = self.backbone(x)
        embedded_x = self.model(x)
        return embedded_x

class TripletNet(pl.LightningModule):
    def __init__(self, embed_net):
        super(TripletNet, self).__init__()
        self.embed_net = embed_net

    def forward(self, a, p, n):
        embedded_a = self.embed_net(a)
        embedded_p = self.embed_net(p)
        embedded_n = self.embed_net(n)
        return embedded_a, embedded_p, embedded_n

    def feature_extract(self, x):
        return self.embed_net(x)

class LightningTripletNet(pl.LightningModule):
    def __init__(self):
        super(LightningTripletNet, self).__init__()
        backbone = get_backbone('resnet18')
        model = get_model('netvlad')
        self.embed_net = EmbedNet(backbone, model)
        self.triplet_net = TripletNet(self.embed_net)

    def feature_extract(self, x):
        return self.embed_net(x)

    
    

